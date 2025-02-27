package jwt_practice.springjwt.repository;

import jakarta.transaction.Transactional;
import jwt_practice.springjwt.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refresh);

    @Transactional //커스텀한 메서드는 트랜잭션 적용해야 한다 !
    void deleteByRefresh(String refresh);
}
