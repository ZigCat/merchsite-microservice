package com.github.zigcat.merchsite_microservice.main.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.zigcat.merchsite_microservice.main.dto.requests.AuthRequest;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
        String bearer = request.getHeader("Authorization");
        try {
            if(bearer != null && bearer.startsWith("Bearer ")) {
                AuthRequest authRequest = new AuthRequest(bearer.substring(7));
                String requestJson = authRequestSerializer.serialize(authRequest);
                String responseJson = kafkaProducerService.sendUserForAuth(requestJson);
                if(responseJson.startsWith("Error ")){
                    throw new IllegalStateException("Auth server error occurred");
                } else {
                    AppUser user = userDeserializer.deserialize(responseJson);
                    if (user.getEmail() != null) {
                        AppUserDetails userDetails = new AppUserDetails(user);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExecutionException | InterruptedException | IllegalStateException e) {
            log.warn(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }
    }
}
