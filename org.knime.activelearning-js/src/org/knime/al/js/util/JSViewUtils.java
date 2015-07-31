/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Jul 3, 2015 (gabriel): created
 */
package org.knime.al.js.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 *
 * @author gabriel
 */
public final class JSViewUtils {

    private JSViewUtils() {
        // NB Util class
    }

    private static final String VALUE = "value_";
    private static final String KEY = "key_";
    private static final String NUM_SETTINGS = "numSettings";

    /**
     * Stores a <String, String> Map to the given settings .
     *
     * @param settings
     *            the node settings
     * @param map
     *            the map to save
     */
    public static void saveMap(final NodeSettingsWO settings,
            final Map<String, String> map) {
        settings.addInt(NUM_SETTINGS, map.size());
        int i = 0;
        for (final Entry<String, String> entry : map.entrySet()) {
            settings.addString(KEY + i, entry.getKey());

            final String value = entry.getValue();
            final String valueKey = VALUE + i;
            if (value == null) {
                settings.addString(valueKey, null);
                continue;
            } else {
                settings.addString(valueKey, value);
                i++;
            }
        }
    }

    /**
     * Loads a map from the NodeSettings.
     *
     * @param settings
     *            the NodeSettings
     * @return the map
     * @throws InvalidSettingsException
     *             if the settings are invalid
     */
    public static Map<String, String> loadMap(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        final int numSettings = settings.getInt(NUM_SETTINGS);
        final Map<String, String> map = new HashMap<>(numSettings);
        for (int i = 0; i < numSettings; i++) {
            final String key = settings.getString(KEY + i);
            final String value = settings.getString(VALUE + i);
            map.put(key, value);
        }
        return map;
    }

    /**
     * saves a list to the NodeSettings.
     *
     * @param settings
     *            the node Settings
     * @param list
     *            the list
     */
    public static void saveList(final NodeSettingsWO settings,
            final List<String> list) {
        settings.addInt(NUM_SETTINGS, list.size());
        int i = 0;
        for (final String entry : list) {
            final String key = KEY + i;
            settings.addString(key, entry);
            i++;
        }
    }

    /**
     * Loads a list from the node settings.
     *
     * @param settings
     *            the nodesettings
     * @return the loaded list
     * @throws InvalidSettingsException
     *             if the settings are invalids
     */
    public static List<String> loadList(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        final int numSettings = settings.getInt(NUM_SETTINGS);
        final List<String> list = new ArrayList<>(numSettings);
        for (int i = 0; i < numSettings; i++) {
            list.add(settings.getString(KEY));
        }
        return list;
    }

    /**
     * Saves a set to the node settings.
     *
     * @param settings
     *            the node settings
     * @param set
     *            the set to save
     */
    public static void saveSet(final NodeSettingsWO settings,
            final Set<String> set) {
        settings.addInt(NUM_SETTINGS, set.size());
        int i = 0;
        for (final String entry : set) {
            final String key = KEY + i;
            settings.addString(key, entry);
            i++;
        }
    }

    /**
     * Loads a set from the node settings.
     *
     * @param settings
     *            the node settings
     * @return the loaded set
     * @throws InvalidSettingsException
     */
    public static Set<String> loadSet(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        final int numSettings = settings.getInt(NUM_SETTINGS);
        final Set<String> set = new HashSet<>(numSettings);
        for (int i = 0; i < numSettings; i++) {
            set.add(settings.getString(KEY));
        }
        return set;
    }

}
