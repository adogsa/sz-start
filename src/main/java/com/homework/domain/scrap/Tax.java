package com.homework.domain.scrap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long totalSalary; // 총급여
    private Long calculatedTax; // 산출세액
    private Long insuranceAmount; // 보험료
    private Long medicalAmount; // 의료비
    private Long educationAmount; // 교육비
    private Long donationAmount;   // 기부금
    private Double retirementAmount; // 퇴직 연금

    private String userId;

    public Double getEarnedIncomeDeduction() {
        double earnedIncome = this.calculatedTax * 0.55; // 근로 소득 세액 공제 금액

        // 의료비 공제 금액
        double medicalTax = (this.medicalAmount - this.totalSalary * 0.03) * 0.15; // 의료비 공제 금액
        medicalTax = medicalTax < 0 ? 0 : medicalTax;

        // 특별 세액 공제
        double specialTax =
                this.insuranceAmount * 0.12 // 보험료 공제 금액
                        + medicalTax // 의료비 공제 금액
                        + this.educationAmount * 0.15 // 교육비 공제 금액
                        + this.donationAmount * 0.15; // 기부금 공제 금액
        specialTax = specialTax < 130000 ? 0 : specialTax;

        // 표준세액공제금액
        double standardTax = specialTax < 130000 ? 130000 : 0;

        // 퇴직연금 세액공제 금액
        double retirementTax = this.retirementAmount * 0.15;

        //결정 세액
        double result = this.calculatedTax - earnedIncome - specialTax - standardTax - retirementTax;

        return result < 0 ? 0 : result;
    }
}
