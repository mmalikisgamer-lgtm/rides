package com.example.omniride.service

object SafetyService {

    /**
     * Mask sensitive identity documents such as CNIC or driving license to protect privacy
     * e.g., "12345-6789012-3" -> "12345-*******-3"
     */
    fun maskIdentityDocument(rawNumber: String): String {
        if (rawNumber.length < 6) return "***"
        val prefix = rawNumber.take(3)
        val suffix = rawNumber.takeLast(2)
        val stars = "*".repeat(rawNumber.length - 5)
        return "$prefix$stars$suffix"
    }

    /**
     * Mask phone number for customer-driver communications
     * e.g. "+14155552671" -> "+1 (415) ***-2671"
     */
    fun maskPhoneNumber(rawPhone: String): String {
        if (rawPhone.length < 7) return "+* *** ***"
        val clean = rawPhone.filter { it.isDigit() }
        val last4 = clean.takeLast(4)
        return "+1 (***) ***-$last4 (Masked Proxy)"
    }

    /**
     * Generate shareable live trip link for loved ones
     */
    fun generateTripShareUrl(rideId: String): String {
        return "https://omniride.app/trip/share?token=omni_${rideId.take(8)}"
    }
}
