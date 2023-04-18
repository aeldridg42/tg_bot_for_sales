package bot.telegram.repositories;

import bot.telegram.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByUrl(String url);
    Optional<Product> findByName(String name);
    boolean existsByUrl(String url);
}