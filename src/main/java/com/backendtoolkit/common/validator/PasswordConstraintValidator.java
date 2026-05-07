package com.backendtoolkit.common.validator;

import java.util.Arrays;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;

import com.google.common.base.Joiner;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	/**
	 * Validates the given password against a set of defined rules to ensure it
	 * meets acceptable security criteria.
	 *
	 * @param password the password to be validated
	 * @param context  the context in which the constraint is evaluated
	 * @return {@code true} if the password is valid according to the defined rules,
	 *         {@code false} otherwise
	 *
	 *         <p>
	 *         The following rules are applied to validate the password:
	 *         </p>
	 *         <ul>
	 *         <li>LengthRule: Password must be between 8 and 32 characters
	 *         long.</li>
	 *         <li>CharacterRule(UpperCase): Password must contain at least one
	 *         uppercase letter.</li>
	 *         <li>CharacterRule(Digit): Password must contain at least one
	 *         digit.</li>
	 *         <li>CharacterRule(Special): Password must contain at least one
	 *         special character.</li>
	 *         </ul>
	 *         <li>NoAccentsOrSpaceRule: Password must not contain whitespaces or
	 *         accents.</li>
	 *         <p>
	 *         If the password does not meet one or more of these criteria, the
	 *         method returns {@code false} and the context is updated with
	 *         violation messages indicating which rules were broken.
	 *         </p>
	 */
	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password == null) {
			return true;
		}

		var validator = new PasswordValidator(Arrays.asList(new LengthRule(8, 32),
				new CharacterRule(EnglishCharacterData.UpperCase, 1), new CharacterRule(EnglishCharacterData.Digit, 1),
				new CharacterRule(EnglishCharacterData.Special, 1), new NoAccentsOrSpacesRule()));

		RuleResult result = validator.validate(new PasswordData(password));
		if (result.isValid()) {
			return true;
		}
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(Joiner.on(", ").join(validator.getMessages(result)))
				.addConstraintViolation();
		return false;
	}
}
