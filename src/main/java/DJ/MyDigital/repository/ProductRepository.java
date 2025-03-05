package DJ.MyDigital.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import DJ.MyDigital.Model.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query to retrieve products by Merchant ID
    @Query("SELECT p FROM Product p WHERE p.farmer.id = :farmerId")
    List<Product> findByFarmerId(@Param("farmerId") Long farmerId);
}
