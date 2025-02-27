package jwt_practice.springjwt.jwt;

import jwt_practice.springjwt.entity.UserEntity;
import jwt_practice.springjwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //파라미터로 넘어온 username이 DB에 해당 객체가 있는지를 찾아주는 메소드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //DB에서 조회
        UserEntity userData = userRepository.findByUsername(username);

        //UserDetails에 담아서 return 하면 AuthenticationManager가 검증을 시작한다.
        if (userData != null) {
            return new CustomUserDetails(userData);
        }

        return null;
    }
}

