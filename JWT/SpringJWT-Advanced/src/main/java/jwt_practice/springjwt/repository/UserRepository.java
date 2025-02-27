package jwt_practice.springjwt.repository;

import jwt_practice.springjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);

    //username을 받아 DB 테이블에서 회원을 조회하는 메서드 작성
    UserEntity findByUsername(String username);
}
