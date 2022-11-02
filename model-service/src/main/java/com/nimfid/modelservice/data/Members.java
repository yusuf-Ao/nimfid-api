package com.nimfid.modelservice.data;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Data
@RequiredArgsConstructor
@SuperBuilder
@Entity
public class Members {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(
            name = "members_id_sequence",
            sequenceName = "members_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "members_id_sequence"
    )
    private Long                    id;
    private Long                    menMembers;
    private Long                    womenMembers;
    private Long                    physicallyDisabledMembers;
    private Long                    youthMembers;
    private Long                    totalMembers;
}
