package com.backendtoolkit.common.validator;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.passay.PasswordData;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;

public class NoAccentsOrSpacesRule implements Rule {

	@Override
	public RuleResult validate(PasswordData passwordData) {
		String password = passwordData.getPassword();
		var result = new RuleResult(true);

		if (password == null || password.isEmpty()) {
			return result;
		}

		if (containsWhitespace(password)) {
			result.setValid(false);
			result.getDetails().add(new RuleResultDetail("Password must not contains whitespaces", null));
		}

		if (!Normalizer.isNormalized(password, Form.NFD)) {
			result.setValid(false);
			result.getDetails().add(new RuleResultDetail("Password must not contains accents", null));
		}

		return result;
	}

	private boolean containsWhitespace(String s) {
		for (char c : s.toCharArray()) {
			if (Character.isWhitespace(c)) {
				return true;
			}
		}
		return false;
	}
}