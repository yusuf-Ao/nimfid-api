package com.nimfid.modelservice.data;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Data
@RequiredArgsConstructor
@SuperBuilder
@Entity
public class SavingsMobilized {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(
            name = "savings_id_sequence",
            sequenceName = "savings_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "savings_id_sequence"
    )
    private Long                    id;
    private Long                    menSavings;
    private Long                    womenSavings;
    private Long                    physicallyDisabledSavings;
    private Long                    youthSavings;
    private Long                    totalSavings;

}
