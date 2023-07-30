package upbrella.be.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import upbrella.be.store.dto.response.*;
import upbrella.be.store.entity.*;

import java.util.List;
import java.util.stream.Collectors;

import static upbrella.be.store.entity.QClassification.classification;
import static upbrella.be.store.entity.QStoreDetail.storeDetail;
import static upbrella.be.store.entity.QStoreImage.storeImage;
import static upbrella.be.store.entity.QStoreMeta.storeMeta;

@RequiredArgsConstructor
public class StoreMetaRepositoryImpl implements StoreMetaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SingleStoreResponse> findAllStores() {

        List<StoreDetail> storeDetails = queryFactory
                .selectFrom(storeDetail)
                .join(storeDetail.storeMeta, storeMeta).fetchJoin()
                .join(storeMeta.classification, classification).fetchJoin()
                .join(storeMeta.subClassification, classification).fetchJoin()
                .leftJoin(storeDetail.storeImages, storeImage).fetchJoin()
                .fetch();

        return storeDetails.stream()
                .map(storeDetail -> new SingleStoreResponse(storeDetail))
                .collect(Collectors.toList());
    }
}