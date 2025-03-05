package DJ.MyDigital.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import DJ.MyDigital.Model.MProduct;


@Repository
public interface MProductRepository extends JpaRepository<MProduct, Long> {

    // Custom query to retrieve products by Merchant ID
    @Query("SELECT p FROM MProduct p WHERE p.merchant.id = :merchantId")
    List<MProduct> findProductsByMerchantId(@Param("merchantId") Long merchantId);
}
