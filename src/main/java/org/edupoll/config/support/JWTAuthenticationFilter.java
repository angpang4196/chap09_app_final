package org.edupoll.config.support;

import java.io.IOException;
import java.util.List;

import org.edupoll.service.JWTService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private final JWTService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 사용자가 JWT 토큰을 가지고 왔다면
		String authorization = request.getHeader("Authorization");
		log.info("Authorization header value : {}", authorization);

		if (authorization == null) {
			log.info("Did not process authentication request since failed to find authorization header");
			filterChain.doFilter(request, response); // 통과시켜주면 됨.
			return;
		}

		try {
			// JWT 유효성 검사 해서 통과하면
			String email = jwtService.verifyToken(authorization);

			// 여기까지 왔으면 통과를 한거임.
			Authentication authentication = new UsernamePasswordAuthenticationToken(email, authorization,
					List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
			// principal >>> 인증 주체자 정보(UserDetails 객체가 보통 설정됨)
			// @AuthenticationPrincipal 했을 때 나오는 값

			// credential >>> 인증에 사용됐던 정보(크게 상관없음)

			// authorities >>> 권한 : role 에 따른 차단 설정을 할 때 씀

			// 위에서 만들어진 인증 객체 등록
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			// 토큰이 만료되었거나 위조된 상황
			log.error("Verify token failed . {}", e.getMessage());
			throw new BadCredentialsException("Invalid authentication token");
		}

		// 인증통과 상태로 만들어 버리자.
		// log.info("{}", authentication);

		filterChain.doFilter(request, response);
	}

}
