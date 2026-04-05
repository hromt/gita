package com.gita.server.repository;

import com.gita.server.dto.ItemWithOwnerDto;
import com.gita.server.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemEntityRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerIdOrderByNameAsc(Long ownerId);

    int countByOwnerId(Long ownerId);

    @Query("SELECT COALESCE(AVG(i.estimatedValue), 0) FROM Item i WHERE i.ownerId = :ownerId")
    double averageEstimatedValueForOwner(@Param("ownerId") Long ownerId);

    @Query("""
            SELECT new com.gita.server.dto.ItemWithOwnerDto(
              i.id, i.ownerId, u.username, i.name, i.description, i.category, i.subcategory,
              i.estimatedValue, i.lookingToTrade, i.imageUri)
            FROM Item i INNER JOIN User u ON i.ownerId = u.id
            WHERE i.ownerId <> :excludeUserId
            AND (:includeNonTrade = true OR i.lookingToTrade = true)
            AND (trim(coalesce(:query, '')) = ''
                 OR lower(i.name) LIKE lower(concat('%', :query, '%'))
                 OR lower(i.category) LIKE lower(concat('%', :query, '%')))
            ORDER BY i.name ASC
            """)
    List<ItemWithOwnerDto> searchMarketplace(
            @Param("excludeUserId") Long excludeUserId,
            @Param("includeNonTrade") boolean includeNonTrade,
            @Param("query") String query
    );

    @Modifying
    @Query("UPDATE Item i SET i.ownerId = :newOwner WHERE i.id IN :ids")
    int updateOwnerForItems(@Param("ids") List<Long> itemIds, @Param("newOwner") Long newOwnerId);
}
