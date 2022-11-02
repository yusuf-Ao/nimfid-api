package com.nimfid.modelservice.data;

import com.nimfid.commons.enums.OrganizationStatus;
import com.nimfid.commons.enums.OrganizationType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;

@Data
@RequiredArgsConstructor
@SuperBuilder
@Entity
public class OrganizationModel {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(
            name = "orgmodel_id_sequence",
            sequenceName = "orgmodel_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "orgmodel_id_sequence"
    )
    private Long                    id;
    @NotEmpty
    @Column(nullable = false, unique = true)
    private String                  orgUID;
    @NotEmpty
    @Column(nullable = false, unique = true)
    private String                  orgName;
    @Enumerated(EnumType.STRING)
    private OrganizationType        organizationType;
    @Enumerated(EnumType.STRING)
    private OrganizationStatus      organizationStatus;
    private ZonedDateTime           dateRegistered;
    private ZonedDateTime           lastModified;
    private String                  authorizedRepresentativeId;
    private Long                    capitalOwned;
    @OneToOne
    @JoinColumn(name = "contact_details_id")
    private ContactDetails          contactDetails;
    @OneToOne
    @JoinColumn(name = "affiliation_details_id")
    private AffiliationDetails      affiliationDetails;
    @OneToOne
    @JoinColumn(name = "members_id")
    private Members                 members;
    @OneToOne
    @JoinColumn(name = "outstanding_portfolio_id")
    private OutstandingPortfolio    outstandingPortfolio;
    @OneToOne
    @JoinColumn(name = "savings_mobilized_id")
    private SavingsMobilized        savingsMobilized;




}
