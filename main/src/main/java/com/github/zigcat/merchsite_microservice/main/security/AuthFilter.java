package com.github.zigcat.merchsite_microservice.main.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.zigcat.merchsite_microservice.main.dto.requests.AuthRequest;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.exceptions.AuthServerErrorException;
import com.github.zigcat.merchsite_microservice.main.exceptions.AuthenticationErrorException;
import com.github.zigcat.merchsite_microservice.main.exceptions.RecordNotFoundException;
import com.github.zigcat.merchsite_microservice.main.kafka.KafkaProducerService;
import com.github.zigcat.merchsite_microservice.main.security.user.AppUserDetails;
import com.github.zigcat.merchsite_microservice.main.services.jackson.AppDeserializer;
import com.github.zigcat.merchsite_microservice.main.services.jackson.AppSerializer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.KafkaReplyTimeoutException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class AuthFilter extends OncePerRequestFilter {
    private final AppSerializer<AuthRequest> authRequestSerializer;
    private final AppDeserializer<AppUser> userDeserializer;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public AuthFilter(AppSerializer<AuthRequest> authRequestSerializer,
                      AppDeserializer<AppUser> userDeserializer,
                      KafkaProducerService kafkaProducerService) {
        this.authRequestSerializer = authRequestSerializer;
        this.userDeserializer = userDeserializer;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        log.info("----------------------");
        log.info("AuthFilter initiated");
        String bearer = request.getHeader("Authorization");
        try {
            log.info("Checking token type...");
            if(bearer != null && bearer.startsWith("Bearer ")) {
                log.info("Token type accepted by filter");
                AuthRequest authRequest = new AuthRequest(bearer.substring(7));
                log.info("Serializing AuthRequest...");
                String requestJson = authRequestSerializer.serialize(authRequest);
                log.info("Sending AuthRequest to AUTH server...");
                String responseJson = kafkaProducerService.sendUserForAuth(requestJson);
                log.info("Received User from AUTH server");
                log.info("Checking errors...");
                if(responseJson.startsWith("Error ")){
                    if(responseJson.substring(6).equals("401")){
                        throw new AuthenticationErrorException("User");
                    } else {
                        throw new AuthServerErrorException();
                    }
                } else {
                    log.info("Errors not found");
                    log.info("Deserializing User...");
                    AppUser user = userDeserializer.deserialize(responseJson);
                    log.info("Checking whether User null or not...");
                    if (user.getEmail() != null) {
                        log.warn("User is present, authorizing request");
                        AppUserDetails userDetails = new AppUserDetails(user);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.warn("User null, request authorizing rejected");
                    }
                }
            } else {
                log.warn("Token type invalid or absent");
            }
            log.warn("Passing filterChain...");
            filterChain.doFilter(request, response);
        } catch (AuthServerErrorException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        } catch (AuthenticationErrorException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }
}
