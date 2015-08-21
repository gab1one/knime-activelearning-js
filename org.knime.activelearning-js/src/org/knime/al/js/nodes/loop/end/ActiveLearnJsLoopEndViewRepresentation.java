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
package org.knime.al.js.nodes.loop.end;

import java.util.List;

import org.knime.al.js.util.JSViewUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author gabriel
 *
 *
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ActiveLearnJsLoopEndViewRepresentation extends JSONViewContent {

    private static final String SERVERPORT = "server_port";
    private static final String ROWIDS = "row_ids";
    private static final String FORMAT = "format";

    private List<String> m_rowIDs;
    private int m_serverPort;
    private String m_format;

    /**
     * Serialization constructor do not use!
     */
    public ActiveLearnJsLoopEndViewRepresentation() {
    }

    /**
     * @param rowIDs
     *            the rows ids.
     * @param serverPort
     *            the server port
     * @param format
     *            the format of the representation
     */
    public ActiveLearnJsLoopEndViewRepresentation(final List<String> rowIDs,
            final int serverPort, final String format) {
        super();
        m_rowIDs = rowIDs;
        m_serverPort = serverPort;
        m_format = format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        JSViewUtils.saveList(settings.addNodeSettings(ROWIDS), m_rowIDs);
        settings.addInt(SERVERPORT, m_serverPort);
        settings.addString(m_format, FORMAT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_rowIDs = JSViewUtils.loadList(settings.getNodeSettings(ROWIDS));
        m_serverPort = settings.getInt(SERVERPORT);
        m_format = settings.getString(FORMAT);
    }

    public String getFormat() {
        return m_format;
    }

    public void setFormat(final String format) {
        m_format = format;
    }

    /**
     * @return the m_rowRepresentations
     */
    public List<String> getRowIDs() {
        return m_rowIDs;
    }

    /**
     * @return the m_serverPort
     */
    public int getServerPort() {
        return m_serverPort;
    }

    /**
     * @param serverPort
     *            the m_serverPort to set
     */
    public void setServerPort(final int serverPort) {
        m_serverPort = serverPort;
    }

    /**
     * @param rowIDs
     *            the row Representations to set
     */
    public void setRowIDs(final List<String> rowIDs) {
        m_rowIDs = rowIDs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((m_format == null) ? 0 : m_format.hashCode());
        result = prime * result
                + ((m_rowIDs == null) ? 0 : m_rowIDs.hashCode());
        result = prime * result + m_serverPort;
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
        final ActiveLearnJsLoopEndViewRepresentation other = (ActiveLearnJsLoopEndViewRepresentation) obj;
        if (m_format == null) {
            if (other.m_format != null) {
                return false;
            }
        } else if (!m_format.equals(other.m_format)) {
            return false;
        }
        if (m_rowIDs == null) {
            if (other.m_rowIDs != null) {
                return false;
            }
        } else if (!m_rowIDs.equals(other.m_rowIDs)) {
            return false;
        }
        if (m_serverPort != other.m_serverPort) {
            return false;
        }
        return true;
    }
}
