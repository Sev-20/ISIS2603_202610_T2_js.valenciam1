package co.edu.uniandes.dse.TallerPruebas.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.TallerPruebas.entities.UserEntity;

/**
 * Interface that persists a user
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByCorreo(String correo);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByCorreo(String correo);

    boolean existsByUsername(String username);

    List<UserEntity> findByNombreContainingIgnoreCase(String nombre);

}
