/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * bahlef - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.java.google;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.api.annotations.common.CheckForNull;

import com.google.common.collect.Range;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;

import de.funfried.netbeans.plugins.external.formatter.exceptions.FormattingFailedException;

/**
 * Delegation class to the Google formatter implementation.
 *
 * @author bahlef
 */
public final class GoogleJavaFormatterWrapper {
	/**
	 * Package private Constructor for creating a new instance of {@link GoogleJavaFormatterWrapper}.
	 */
	GoogleJavaFormatterWrapper() {
	}

	/**
	 * Formats the given {@code code} with the given configurations and returns
	 * the formatted code.
	 *
	 * @param code the unformatted code
	 * @param codeStyle the code {@link JavaFormatterOptions.Style} to use,
	 *        if {@code null} the {@link JavaFormatterOptions.Style#GOOGLE}
	 *        style will be used
	 * @param changedElements a {@link SortedSet} containing ranges as {@link Pair}
	 *        objects defining the offsets which should be formatted
	 *
	 * @return the formatted code
	 *
	 * @throws FormattingFailedException if the external formatter failed to format the given code
	 */
	@CheckForNull
	public String format(String code, JavaFormatterOptions.Style codeStyle, SortedSet<Pair<Integer, Integer>> changedElements) throws FormattingFailedException {
		if (code == null) {
			return null;
		}

		if (codeStyle == null) {
			codeStyle = JavaFormatterOptions.Style.GOOGLE;
		}

		Collection<Range<Integer>> characterRanges = new ArrayList<>();
		if (changedElements == null) {
			characterRanges.add(Range.closedOpen(0, code.length()));
		} else if (!CollectionUtils.isEmpty(changedElements)) {
			for (Pair<Integer, Integer> changedElement : changedElements) {
				int start = changedElement.getLeft();
				int end = changedElement.getRight();

				if (start == end) {
					end++;
				}

				characterRanges.add(Range.open(start, end));
			}
		} else {
			// empty changed elements means nothing's left which can be formatted due to guarded sections
			return code;
		}

		try {
			Formatter formatter = new Formatter(JavaFormatterOptions.builder().style(codeStyle).build());
			return formatter.formatSource(code, characterRanges);
		} catch (FormatterException ex) {
			throw new FormattingFailedException(ex);
		}
	}
}
