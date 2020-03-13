package com.duallab.verapdf.jobservice.model.entity.enums;
import org.verapdf.pdfa.flavours.PDFAFlavour;

/**
 * @author Maksim Bezrukov
 */
public enum Profile {
	TAGGED_PDF_1_7("/profiles/tagged_1-7.xml", null),
	TAGGED_PDF_2_0("/profiles/tagged_2-0.xml", null),
	PDFA_1_A(null, PDFAFlavour.PDFA_1_A),
	PDFA_1_B(null, PDFAFlavour.PDFA_1_B),
	PDFA_2_A(null, PDFAFlavour.PDFA_2_A),
	PDFA_2_B(null, PDFAFlavour.PDFA_2_B),
	PDFA_2_U(null, PDFAFlavour.PDFA_2_U),
	PDFA_3_A(null, PDFAFlavour.PDFA_3_A),
	PDFA_3_B(null, PDFAFlavour.PDFA_3_B),
	PDFA_3_U(null, PDFAFlavour.PDFA_3_U),
	PDFA_AUTO(null, null);

	private String resourcePath;
	private PDFAFlavour pdfaFlavour;

	Profile(String resourcePath, PDFAFlavour pdfaFlavour) {
		this.resourcePath = resourcePath;
		this.pdfaFlavour = pdfaFlavour;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public PDFAFlavour getPdfaFlavour() {
		return pdfaFlavour;
	}
}
