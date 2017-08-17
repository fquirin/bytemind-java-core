package de.bytemind.core.statics;

import java.util.Objects;
import java.util.Optional;

/**
 * Constants for languages for better type safety in Java (so that e.g. "en" isn't just
 * a String that gets easily confused with other strings).
 */
public enum Language {

	EN("English", "English", "eng"),
	DE("German", "Deutsch", "deu"),
	AR("Arabic", null, "ara"),
	EL("Greek", null, "ell"),
	ES("Spanish", null, "spa"),
	FR("French", null, "fra"),
	HR("Croatian", null, "hrv"),
	IT("Italian", null, "ita"),
	JA("Japanese", null, "jpn"),
	KO("Korean", null, "kor"),
	NL("Dutch", null, "nld"),
	PL("Polish", null, "pol"),
	PT("Portuguese", null, "por"),
	RU("Russian", null, "rus"),
	SV("Swedish", null, "swe"),
	TR("Turkish", null, "tur"),
	ZH("Chinese", null, "cmn");
	

	/**
	 * Get enum from 2-letter language code. Converts value to upper-case.
	 */
	public static Language fromValue(String value) {
		return Language.valueOf(value.toUpperCase());
	}

	/**
	 * Use this to get e.g. "de" for German, "en" for English.
	 */
	public String toValue() {
		return name().toLowerCase();
	}
	
	private final String englishFullName;
	private final String translatedFullName;

	private final String tatoebaCode;

	Language(String englishFullName, String translatedFullName, String tatoebaCode) {
		this.englishFullName = Objects.requireNonNull(englishFullName);
		this.translatedFullName = translatedFullName;
		this.tatoebaCode = Objects.requireNonNull(tatoebaCode);
	}

	public String getTranslatedFullName() {
		return translatedFullName;
	}

	public Optional<String> getEnglishFullName() {
		return Optional.ofNullable(englishFullName);
	}

	/**
	 * A three-character code as used by tatoeba (ISO 639-3).
	 */
	public String getTatoebaCode() {
		return tatoebaCode;
	}
	
}
