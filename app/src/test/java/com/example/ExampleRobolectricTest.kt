package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.omniride.data.model.PaymentMethod
import com.example.omniride.service.LocationMapService
import com.example.omniride.service.PaymentService
import com.example.omniride.service.SafetyService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("OmniRide", appName)
    }

    @Test
    fun `verify zero percent platform commission calculation`() {
        val breakdown = PaymentService.computeFareBreakdown(500.0, PaymentMethod.CASH)
        assertEquals(500.0, breakdown.grossFare, 0.01)
        assertEquals(0.0, breakdown.platformCommissionRate, 0.001)
        assertEquals(0.0, breakdown.platformFee, 0.001)
        assertEquals(25.0, breakdown.regulatoryTax, 0.01) // 5% provincial sales tax = Rs. 25
        assertEquals(475.0, breakdown.driverNetEarnings, 0.01) // Driver keeps Rs. 475
    }

    @Test
    fun `verify location distance calculation`() {
        // SF Downtown to Embarcadero ~2.2 km
        val distance = LocationMapService.calculateDistanceKm(37.7749, -122.4194, 37.7892, -122.4014)
        assertTrue(distance > 1.0 && distance < 3.5)
    }

    @Test
    fun `verify privacy document masking`() {
        val masked = SafetyService.maskIdentityDocument("12345-6789012-3")
        assertTrue(masked.contains("***"))
        assertEquals("123", masked.take(3))
    }
}
