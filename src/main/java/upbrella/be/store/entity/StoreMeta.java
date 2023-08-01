package upbrella.be.store.entity;

import lombok.*;
import upbrella.be.store.dto.request.CreateStoreRequest;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private boolean activated;
    private boolean deleted;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classification_id")
    private Classification classification;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_classification_id")
    private Classification subClassification;
    private String category;
    private double latitude;
    private double longitude;
    private String password;
    @OneToMany(mappedBy = "storeMeta", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<BusinessHour> businessHours;

    public void delete() {

        this.deleted = true;
    }

    public static StoreMeta createStoreMetaForSave(CreateStoreRequest request, Classification classification, Classification subClassification) {

        return StoreMeta.builder()
                .name(request.getName())
                .activated(request.isActivateStatus())
                .deleted(false)
                .classification(classification)
                .subClassification(subClassification)
                .category(request.getCategory())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .password(request.getPassword())
                .build();
    }

    public static StoreMeta createStoreMetaForUpdate(CreateStoreRequest request, Classification classification, Classification subClassification, List<BusinessHour> businessHours) {

        return StoreMeta.builder()
                .name(request.getName())
                .activated(request.isActivateStatus())
                .deleted(false)
                .classification(classification)
                .subClassification(subClassification)
                .category(request.getCategory())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .password(request.getPassword())
                .businessHours(businessHours.stream().collect(Collectors.toSet()))
                .build();
    }

    public void updateStoreMeta(StoreMeta storeMeta) {

        this.name = storeMeta.getName();
        this.activated = storeMeta.isActivated();
        this.deleted = storeMeta.isDeleted();
        this.classification = storeMeta.getClassification();
        this.subClassification = storeMeta.getSubClassification();
        this.category = storeMeta.getCategory();
        this.latitude = storeMeta.getLatitude();
        this.longitude = storeMeta.getLongitude();
        this.password = storeMeta.getPassword();
    }
}