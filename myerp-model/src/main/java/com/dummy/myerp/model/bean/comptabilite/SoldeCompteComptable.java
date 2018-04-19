package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class SoldeCompteComptable {
    // ==================== Attributs ====================

    private CompteComptable compteComptable;
    private String typeSolde;
    private BigDecimal sommeCrediteur;
    private BigDecimal sommeDebiteur;
    //Solde débiteur (négatif possible == solde créditeur)
    private BigDecimal solde;
    //Solde relatif (valeurs négatives théoriquement impossible)
    private BigDecimal absSolde;

    // ==================== Constructeurs ====================


    public SoldeCompteComptable(CompteComptable cmptcmptbl, BigDecimal sommeCredit, BigDecimal sommeDebit){
        this.compteComptable = cmptcmptbl;
        this.sommeCrediteur = sommeCredit;
        this.sommeDebiteur = sommeDebit;
        setSolde();
    }

    // ==================== Getters/Setters ====================


    public BigDecimal getSolde() {
        return solde;
    }

    public CompteComptable getCompteComptable() {
        return compteComptable;
    }

    public String getTypeSolde() {
        return typeSolde;
    }

    public BigDecimal getSommeCrediteur() {
        return sommeCrediteur;
    }

    public BigDecimal getSommeDebiteur() {
        return sommeDebiteur;
    }

    public BigDecimal getAbsSolde() {
        return absSolde;
    }


    // ==================== Méthodes ====================

    private void setSolde() {
        //
        BigDecimal sommeDebitProvisoire;
        BigDecimal sommeCreditProvisoire;
        // Gestion des null
        if(sommeDebiteur != null){
            sommeDebitProvisoire = new BigDecimal(this.sommeDebiteur.toPlainString()).setScale(2,RoundingMode.HALF_UP);
        }else{
            sommeDebitProvisoire = new BigDecimal("0").setScale(2,RoundingMode.HALF_UP);
        }
        if(sommeCrediteur != null){
            sommeCreditProvisoire = new BigDecimal(this.sommeCrediteur.toPlainString()).setScale(2, RoundingMode.HALF_UP);
        }else{
            sommeCreditProvisoire = new BigDecimal("0").setScale(2,RoundingMode.HALF_UP);
        }
        //
        this.solde = sommeDebitProvisoire.subtract(sommeCreditProvisoire);
        this.absSolde=this.solde.abs();
        if(this.solde.compareTo(BigDecimal.valueOf(0)) == -1){
            this.typeSolde="créditeur";
        }else{
            this.typeSolde="débiteur";
        }
    }

    // ==================== Méthodes STATIC ====================
}
