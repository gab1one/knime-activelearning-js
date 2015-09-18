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
 *   6 Mar 2015 (gabriel): created
 */
package org.knime.al.js.nodes.webportal.variableloopend;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.knime.al.js.nodes.webportal.ActiveLearnWebPortalNode;
import org.knime.al.nodes.loop.ActiveLearnLoopEnd;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.FlowVariable;

/**
 *
 * @author <a href="mailto:gabriel.einsdorf@uni.kn">Gabriel Einsdorf</a>
 */
public class ActiveLearnVariableLoopEndNodeModel extends NodeModel
        implements ActiveLearnLoopEnd, ActiveLearnWebPortalNode {

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(ActiveLearnVariableLoopEndNodeModel.class);

    private static final int LEARNING_PORT = 0;
    private static final int PASSTHROUGH_PORT = 1;

    private final Map<RowKey, String> m_newlyLabeledRows = new HashMap<>();

    /**
    *
    */
    public ActiveLearnVariableLoopEndNodeModel() {
        super(new PortType[] { BufferedDataTable.TYPE, BufferedDataTable.TYPE },
                new PortType[] { BufferedDataTable.TYPE });
    }

    /**
     * {@inheritDoc} Node routes through the second input port.
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        return new DataTableSpec[] { inSpecs[PASSTHROUGH_PORT] };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] in,
            final ExecutionContext exec) throws Exception {

        final FlowVariable controlVar = getAvailableFlowVariables()
                .get(ALControlVar);

        if (controlVar == null) {
            throw new InvalidSettingsException("Annotator Node not connected");
        }

        final int setting = getAvailableFlowVariables().get(ALControlVar)
                .getIntValue();
        final int column = 0;

        if (setting == 1) {

            m_newlyLabeledRows.clear();

            in[LEARNING_PORT].forEach((final DataRow row) -> {
                m_newlyLabeledRows.put(row.getKey(),
                        ((StringValue) row.getCell(column)).getStringValue());
            });
            super.continueLoop();
            return null;
        } else if (setting == 0) {
            return new BufferedDataTable[] { in[PASSTHROUGH_PORT] };
        } else {
            throw new IllegalArgumentException(
                    "Value of the control Variable mus be either one or zero but was: "
                            + setting);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<RowKey, String> getNewlyLabeledRows() {
        return m_newlyLabeledRows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        // collectSettingsModels();
        // m_settingsModels.forEach((model) -> model.saveSettingsTo(settings));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // collectSettingsModels();
        // for (final SettingsModel model : m_settingsModels) {
        // model.validateSettings(settings);
        // }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir,
            final ExecutionMonitor exec)
                    throws IOException, CanceledExecutionException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir,
            final ExecutionMonitor exec)
                    throws IOException, CanceledExecutionException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {

    }
}
