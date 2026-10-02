package com.medicore;

import com.medicore.dto.AuthDtos;
import com.medicore.dto.PharmacyDtos;
import com.medicore.service.AuthService;
import com.medicore.service.PharmacyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MediCoreApplicationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private PharmacyService pharmacyService;

    @Test
    @DisplayName("Should successfully authenticate administrator")
    void testAdminAuthentication() {
        AuthDtos.LoginRequest req = new AuthDtos.LoginRequest();
        req.setUsername("admin");
        req.setPassword("AdminPassword123!");

        AuthDtos.JwtResponse response = authService.login(req);
        assertNotNull(response.getToken());
        assertEquals("admin", response.getUsername());
        assertTrue(response.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Should create medicine, stock batches, and execute FEFO checkout properly")
    void testPharmacyPOSWorkflow() {
        // 1. Create Category
        PharmacyDtos.CategoryDto catDto = new PharmacyDtos.CategoryDto();
        catDto.setName("Antibiotics");
        PharmacyDtos.CategoryDto cat = pharmacyService.createCategory(catDto);

        // 2. Create Medicine (10 tablets per strip)
        PharmacyDtos.MedicineDto medDto = new PharmacyDtos.MedicineDto();
        medDto.setSku("MED-AMOX-500");
        medDto.setBrandName("Amoxil 500mg");
        medDto.setGenericName("Amoxicillin");
        medDto.setCategoryId(cat.getId());
        medDto.setDosageForm("Capsule");
        medDto.setStrength("500mg");
        medDto.setManufacturer("GSK Healthcare");
        medDto.setUnitsPerStrip(10);
        medDto.setCostPerStrip(new BigDecimal("50.00"));
        medDto.setPricePerStrip(new BigDecimal("80.00"));
        medDto.setTaxPercent(new BigDecimal("5.00"));
        PharmacyDtos.MedicineDto med = pharmacyService.createMedicine(medDto);

        // 3. Inward Batch (10 strips = 100 tablets)
        PharmacyDtos.MedicineBatchDto batchDto = new PharmacyDtos.MedicineBatchDto();
        batchDto.setMedicineId(med.getId());
        batchDto.setBatchNumber("BATCH-AMX-001");
        batchDto.setExpiryDate(LocalDate.now().plusMonths(6));
        batchDto.setQuantityInStrips(10); // 100 units
        pharmacyService.addBatch(batchDto);

        // 4. POS Checkout 2 Strips (20 tablets)
        PharmacyDtos.CheckoutRequest checkoutReq = new PharmacyDtos.CheckoutRequest();
        checkoutReq.setCustomerName("Test Customer");
        checkoutReq.setCustomerPhone("9876543210");
        checkoutReq.setPaymentMethod("CASH");

        PharmacyDtos.InvoiceItemRequest item = new PharmacyDtos.InvoiceItemRequest();
        item.setMedicineId(med.getId());
        item.setQuantityStrips(2);
        checkoutReq.setItems(List.of(item));
        checkoutReq.setPaidAmount(new BigDecimal("168.00")); // 2 * 80 = 160 + 5% tax (8) = 168

        PharmacyDtos.InvoiceResponse invoice = pharmacyService.checkout(checkoutReq);

        assertNotNull(invoice.getInvoiceNumber());
        assertEquals(new BigDecimal("160.00"), invoice.getSubtotal());
        assertEquals(new BigDecimal("8.00"), invoice.getTaxAmount());
        assertEquals(new BigDecimal("168.00"), invoice.getTotalAmount());
        assertEquals("PAID", invoice.getPaymentStatus());

        // 5. Verify remaining stock
        PharmacyDtos.MedicineDto updatedMed = pharmacyService.getMedicineById(med.getId());
        assertEquals(80, updatedMed.getAvailableBaseUnits()); // 100 - 20 = 80
        assertEquals(8, updatedMed.getAvailableStockStrips()); // 80 / 10 = 8 strips
    }
}
