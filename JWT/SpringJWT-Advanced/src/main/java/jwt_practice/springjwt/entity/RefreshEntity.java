package jwt_practice.springjwt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

//발급된 Refresh 토큰을 저장하는 엔티티
@Getter
@Setter
@Entity
public class RefreshEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; //Refresh 토큰이 발급된 유저명
    private String refresh;  //Refresh 토큰
    private String expiration; //Refresh 토큰 유효 기간
}
