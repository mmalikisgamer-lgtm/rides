package com.example.omniride.service

import com.example.omniride.data.model.PaymentMethod
import com.example.omniride.data.model.PaymentStatus

data class FareBreakdown(
    val grossFare: Double,
    val platformCommissionRate: Double = 0.0, // 0.00% Zero platform cut!
    val platformFee: Double = 0.0,
    val regulatoryTaxPercent: Double = 5.0, // 5% Provincial Transport & Services Sales Tax
    val regulatoryTax: Double,
    val paymentGatewayFee: Double,
    val driverNetEarnings: Double
) {
    val grossFormatted: String get() = "Rs. ${String.format("%,.0f", grossFare)}"
    val netFormatted: String get() = "Rs. ${String.format("%,.0f", driverNetEarnings)}"
    val taxFormatted: String get() = "Rs. ${String.format("%,.0f", regulatoryTax)}"
}

object PaymentService {

    fun formatPkr(amount: Double): String {
        return "Rs. ${String.format("%,.0f", amount)}"
    }

    /**
     * Compute transparent fare breakdown in Pakistani Rupees (PKR) with 0% Platform Commission.
     */
    fun computeFareBreakdown(fare: Double, paymentMethod: PaymentMethod): FareBreakdown {
        val tax = Math.round(fare * 0.05).toDouble() // 5% provincial sales tax
        val gatewayFee = if (paymentMethod == PaymentMethod.CARD || paymentMethod == PaymentMethod.DIGITAL_TRANSFER) {
            Math.round(fare * 0.015).toDouble() // 1.5% digital bank/switch transfer fee
        } else {
            0.0
        }
        val netDriver = fare - tax - gatewayFee

        return FareBreakdown(
            grossFare = fare,
            platformCommissionRate = 0.0,
            platformFee = 0.00,
            regulatoryTaxPercent = 5.0,
            regulatoryTax = tax,
            paymentGatewayFee = gatewayFee,
            driverNetEarnings = maxOf(0.0, netDriver)
        )
    }

    /**
     * Simulates payment gateway authorization and wallet settlement (Easypaisa, JazzCash, Raast, Cash).
     */
    fun processPayment(
        amount: Double,
        method: PaymentMethod,
        currentWalletBalance: Double
    ): Pair<PaymentStatus, String> {
        return when (method) {
            PaymentMethod.CASH -> {
                Pair(PaymentStatus.COMPLETED, "Cash on trip completion")
            }
            PaymentMethod.WALLET -> {
                if (currentWalletBalance >= amount) {
                    Pair(PaymentStatus.COMPLETED, "Paid with in-app wallet balance")
                } else {
                    Pair(PaymentStatus.FAILED, "Insufficient wallet balance. Top up via JazzCash/Easypaisa or pay Cash.")
                }
            }
            PaymentMethod.CARD -> {
                Pair(PaymentStatus.COMPLETED, "Bank Card / 1LINK authorization verified")
            }
            PaymentMethod.DIGITAL_TRANSFER -> {
                Pair(PaymentStatus.COMPLETED, "Raast / Digital instant transfer verified")
            }
        }
    }
}
