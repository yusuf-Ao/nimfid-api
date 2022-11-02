package com.nimfid.modelservice.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;


@Data
@RequiredArgsConstructor
@SuperBuilder
@Entity
public class OutstandingPortfolio {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(
            name = "loan_id_sequence",
            sequenceName = "loan_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "loan_id_sequence"
    )
    private Long                    id;
    private Long                    menLoan;
    private Long                    womenLoan;
    private Long                    physicallyDisabledLoan;
    private Long                    youthLoan;
    private Long                    totalLoan;
}
