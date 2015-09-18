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
 *   Jul 2, 2015 (gabriel): created
 */
package org.knime.al.js.nodes.webportal.annotator;

import java.util.Map;
import java.util.Set;

import org.knime.al.js.util.JSViewUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author <a href="mailto:gabriel.einsdorf@uni.kn">Gabriel Einsdorf</a>
 */

@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ActiveLearnAnnotatorViewValue extends JSONViewContent {

    // FIXME Move these to central file
    private static final String ROW_LABELS = "row_labelings_key";
    private static final String CLASS_LABELS = "class_labels_key";
    private static final String CONTINUE_EXC = "continue_execution";

    private Set<String> m_classLabels;
    private Map<String, String> m_rowLabels;
    private boolean m_continueExecution;

    /**
     * Serialization constructor, do not use!
     */
    public ActiveLearnAnnotatorViewValue() {
    }

    /**
     * Creates the ViewValue for the JsLoop end view.
     *
     * @param classLabels
     *            the already known class labels.
     * @param rowLabels
     *            the labels of the rows
     */
    public ActiveLearnAnnotatorViewValue(final Set<String> classLabels,
            final Map<String, String> rowLabels, final boolean continueExecution) {
        super();
        m_classLabels = classLabels;
        m_rowLabels = rowLabels;
        m_continueExecution = continueExecution;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        JSViewUtils.saveMap(settings.addNodeSettings(ROW_LABELS), m_rowLabels);
        JSViewUtils.saveSet(settings.addNodeSettings(CLASS_LABELS),
                m_classLabels);
        settings.addBoolean(CONTINUE_EXC, m_continueExecution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_rowLabels = JSViewUtils.loadMap(settings.getNodeSettings(ROW_LABELS));
        m_classLabels = JSViewUtils
                .loadSet(settings.getNodeSettings(CLASS_LABELS));
        m_continueExecution = settings.getBoolean(CONTINUE_EXC);
    }


    /**
     * @return the continueExecution
     */
    public boolean getContinueExecution() {
        return m_continueExecution;
    }

    /**
     * @param continueExecution the m_continueExecution to set
     */
    public void setContinueExecution(final boolean continueExecution) {
        m_continueExecution = continueExecution;
    }

    /**
     * @return the class labels
     */
    public Set<String> getClassLabels() {
        return m_classLabels;
    }

    /**
     * @param classLabels
     *            the class labels to set
     */
    public void setClassLabels(final Set<String> classLabels) {
        m_classLabels = classLabels;
    }

    /**
     * @return the rowLabels
     */
    public Map<String, String> getRowLabels() {
        return m_rowLabels;
    }

    /**
     * @param rowLabels
     *            the rowLabels to set
     */
    public void setRowLabels(final Map<String, String> rowLabels) {
        m_rowLabels = rowLabels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((m_classLabels == null) ? 0 : m_classLabels.hashCode());
        result = prime * result + (m_continueExecution ? 1231 : 1237);
        result = prime * result
                + ((m_rowLabels == null) ? 0 : m_rowLabels.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ActiveLearnAnnotatorViewValue other = (ActiveLearnAnnotatorViewValue) obj;
        if (m_classLabels == null) {
            if (other.m_classLabels != null) {
                return false;
            }
        } else if (!m_classLabels.equals(other.m_classLabels)) {
            return false;
        }
        if (m_continueExecution != other.m_continueExecution) {
            return false;
        }
        if (m_rowLabels == null) {
            if (other.m_rowLabels != null) {
                return false;
            }
        } else if (!m_rowLabels.equals(other.m_rowLabels)) {
            return false;
        }
        return true;
    }

}
