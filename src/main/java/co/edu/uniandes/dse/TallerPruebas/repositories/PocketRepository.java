package co.edu.uniandes.dse.TallerPruebas.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.TallerPruebas.entities.PocketEntity;

/**
 * Interface that persists a pocket
 */
@Repository
public interface PocketRepository extends JpaRepository<PocketEntity, Long> {

     boolean existsByAccount_IdAndNombreIgnoreCase(Long accountId, String nombre);

    Optional<PocketEntity> findByIdAndAccount_Id(Long pocketId, Long accountId); //Contenedor de java que puede tener un valor o nada 

    //Sirve para evitar null directos y obligar a manejar el caso en que no existe resultado.

    List<PocketEntity> findByAccount_Id(Long accountId);
}
