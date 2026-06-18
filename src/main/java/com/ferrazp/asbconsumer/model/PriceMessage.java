package com.ferrazp.asbconsumer.model;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Date;

public class PriceMessage {

    private String priceListId;
    private String articleId;
    private String description;
    private String currencyId;

    @Positive
    private double amountValue;

    private String taxRuleId;
    private Date activation;
    private Date promotionPriceVto;
    private Date promotionPriceActivation;
    private BigDecimal estampilla;
    private BigDecimal affectedTaxValue;
    private BigDecimal promotionTaxAmount;
    private BigDecimal promotionAffectedTaxAmount;
    private boolean desactivated;
    private BigDecimal promotionAmountValue;
    private String psPriceList;
    private String ipoconsumoTaxRule;
    private BigDecimal oilTaxAmount;

    public String getPriceListId() { return priceListId; }
    public void setPriceListId(String priceListId) { this.priceListId = priceListId; }

    public String getArticleId() { return articleId; }
    public void setArticleId(String articleId) { this.articleId = articleId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCurrencyId() { return currencyId; }
    public void setCurrencyId(String currencyId) { this.currencyId = currencyId; }

    public double getAmountValue() { return amountValue; }
    public void setAmountValue(double amountValue) { this.amountValue = amountValue; }

    public String getTaxRuleId() { return taxRuleId; }
    public void setTaxRuleId(String taxRuleId) { this.taxRuleId = taxRuleId; }

    public Date getActivation() { return activation; }
    public void setActivation(Date activation) { this.activation = activation; }

    public Date getPromotionPriceVto() { return promotionPriceVto; }
    public void setPromotionPriceVto(Date promotionPriceVto) { this.promotionPriceVto = promotionPriceVto; }

    public Date getPromotionPriceActivation() { return promotionPriceActivation; }
    public void setPromotionPriceActivation(Date promotionPriceActivation) { this.promotionPriceActivation = promotionPriceActivation; }

    public BigDecimal getEstampilla() { return estampilla; }
    public void setEstampilla(BigDecimal estampilla) { this.estampilla = estampilla; }

    public BigDecimal getAffectedTaxValue() { return affectedTaxValue; }
    public void setAffectedTaxValue(BigDecimal affectedTaxValue) { this.affectedTaxValue = affectedTaxValue; }

    public BigDecimal getPromotionTaxAmount() { return promotionTaxAmount; }
    public void setPromotionTaxAmount(BigDecimal promotionTaxAmount) { this.promotionTaxAmount = promotionTaxAmount; }

    public BigDecimal getPromotionAffectedTaxAmount() { return promotionAffectedTaxAmount; }
    public void setPromotionAffectedTaxAmount(BigDecimal promotionAffectedTaxAmount) { this.promotionAffectedTaxAmount = promotionAffectedTaxAmount; }

    public boolean isDesactivated() { return desactivated; }
    public void setDesactivated(boolean desactivated) { this.desactivated = desactivated; }

    public BigDecimal getPromotionAmountValue() { return promotionAmountValue; }
    public void setPromotionAmountValue(BigDecimal promotionAmountValue) { this.promotionAmountValue = promotionAmountValue; }

    public String getPsPriceList() { return psPriceList; }
    public void setPsPriceList(String psPriceList) { this.psPriceList = psPriceList; }

    public String getIpoconsumoTaxRule() { return ipoconsumoTaxRule; }
    public void setIpoconsumoTaxRule(String ipoconsumoTaxRule) { this.ipoconsumoTaxRule = ipoconsumoTaxRule; }

    public BigDecimal getOilTaxAmount() { return oilTaxAmount; }
    public void setOilTaxAmount(BigDecimal oilTaxAmount) { this.oilTaxAmount = oilTaxAmount; }
}
