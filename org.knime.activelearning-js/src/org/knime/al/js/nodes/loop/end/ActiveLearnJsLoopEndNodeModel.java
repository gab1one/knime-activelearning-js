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
package org.knime.al.js.nodes.loop.end;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knime.al.nodes.AbstractALNodeModel;
import org.knime.al.nodes.loop.ActiveLearnLoopEnd;
import org.knime.al.nodes.loop.ActiveLearnLoopStart;
import org.knime.al.nodes.loop.ActiveLearnLoopUtils;
import org.knime.al.util.NodeUtils;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelOptionalString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.js.core.JavaScriptViewCreator;

/**
 *
 * @author <a href="mailto:gabriel.einsdorf@uni.kn">Gabriel Einsdorf</a>
 */
public class ActiveLearnJsLoopEndNodeModel extends AbstractALNodeModel
        implements ActiveLearnLoopEnd,
        WizardNode<ActiveLearnJsLoopEndViewRepresentation, ActiveLearnJsLoopViewValue> {

    private final SettingsModelString m_classColModel = ActiveLearnJsLoopEndSettingsModels
            .createClassColumnModel();
    private final SettingsModelString m_repColModel = ActiveLearnJsLoopEndSettingsModels
            .createRepColumnModel();
    private final SettingsModelOptionalString m_defaultClassNameModel = ActiveLearnJsLoopEndSettingsModels
            .createDefaultClassModel();

    private static final int LEARNING_DATA_PORT = 0;
    private static final int PASSTHROUGH_PORT = 1;

    private int m_classColIdx;
    private int m_repColIdx;
    private int m_previousIteration;
    private Map<RowKey, String> m_newLabeledRows;

    private final Object m_lock = new Object();
    private ActiveLearnJsLoopEndViewRepresentation m_representation;
    private ActiveLearnJsLoopViewValue m_viewValue;

    /**
    *
    */
    public ActiveLearnJsLoopEndNodeModel() {
        super(2, 1);
        m_representation = createEmptyViewRepresentation();
        m_viewValue = createEmptyViewValue();

        collectSettingsModels();
    }

    /**
     * {@inheritDoc} Node routes through the second inport
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        m_classColIdx = NodeUtils.autoColumnSelection(
                inSpecs[LEARNING_DATA_PORT], m_classColModel, StringValue.class,
                this.getClass());

        m_repColIdx = NodeUtils.autoColumnSelection(inSpecs[LEARNING_DATA_PORT],
                m_repColModel, DataValue.class, this.getClass());

        return new DataTableSpec[] { inSpecs[PASSTHROUGH_PORT] };
    }

    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        final int currentIteration = getAvailableFlowVariables()
                .get(ActiveLearnLoopUtils.AL_STEP).getIntValue();

        // user pressed the apply button
        if (currentIteration == m_previousIteration) {
            synchronized (m_lock) {

                // TODO: Get new data from view
            }

            super.continueLoop();
            return null;
        }

        m_previousIteration = currentIteration;

        if (inData[LEARNING_DATA_PORT].getRowCount() > 0) {
            synchronized (m_lock) {


                final Class<? extends DataColumnSpec> type = inData[LEARNING_DATA_PORT].getDataTableSpec().getColumnSpec(m_repColIdx).getClass();
                        // Create the State storage
//                final Map<String, RowObject<type>> keyToRowObject = new HashMap<>(
//                        inData[LEARNING_DATA_PORT].getRowCount());

//                for (final DataRow row : inData[LEARNING_DATA_PORT]) {
//                    keyToRowObject.put(row.getKey(), row);
//                }

                final Set<String> classes = ((ActiveLearnLoopStart) getLoopStartNode())
                        .getDefinedClasses();

                // TODO CREATE View Value! View stuff!

            }
        }

        // return an empty table when the optional input is not connected
        if (inData[PASSTHROUGH_PORT] == null) {
            final BufferedDataContainer paddContainer = exec
                    .createDataContainer(new DataTableSpec());
            paddContainer.close();
            return new BufferedDataTable[] { paddContainer.getTable() };
        }
        return new BufferedDataTable[] { inData[PASSTHROUGH_PORT] };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<RowKey, String> getNewlyLabeledRows() {
        return m_newLabeledRows;
    }

    private ColumnRearranger createRepresentativeColumnRearranger(
            final DataTableSpec in) {
        final ColumnRearranger c = new ColumnRearranger(in);
        for (final DataColumnSpec colSpec : in) {
            final DataType type = colSpec.getType();
            if (!type.isCompatible(DoubleValue.class)
                    && !type.isCompatible(StringValue.class)) {
                c.remove(colSpec.getName());
            }
        }
        return c;
    }

    /**
     * Initializes the settingsmodel storage.
     */
    @Override
    protected List<SettingsModel> collectSettingsModels() {
        if (m_settingsModels == null) {
            m_settingsModels = new ArrayList<SettingsModel>(4);
            m_settingsModels.add(m_classColModel);
            m_settingsModels.add(m_defaultClassNameModel);
            m_settingsModels.add(m_repColModel);
        }
        return m_settingsModels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        m_newLabeledRows = null;
        m_previousIteration = -1; // To ensure node gets executed again
        synchronized (m_lock) {
            m_representation = createEmptyViewRepresentation();
            m_viewValue = createEmptyViewValue();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(
            final ActiveLearnJsLoopViewValue viewContent) {
        synchronized (m_lock) {
            // TODO validate value
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final ActiveLearnJsLoopViewValue viewContent,
            final boolean useAsDefault) {
        synchronized (m_lock) {
            m_viewValue = viewContent;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveLearnJsLoopEndViewRepresentation getViewRepresentation() {
        synchronized (m_lock) {
            return m_representation;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveLearnJsLoopViewValue getViewValue() {
        synchronized (m_lock) {
            return m_viewValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveLearnJsLoopEndViewRepresentation createEmptyViewRepresentation() {
        return new ActiveLearnJsLoopEndViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveLearnJsLoopViewValue createEmptyViewValue() {
        return new ActiveLearnJsLoopViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_al_nodes_loop_end2";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewHTMLPath() {
        final JavaScriptViewCreator<ActiveLearnJsLoopEndViewRepresentation, ActiveLearnJsLoopViewValue> viewCreator = new JavaScriptViewCreator<>(
                getJavascriptObjectID());
        try {
            return viewCreator.createWebResources("View",
                    getViewRepresentation(), getViewValue());
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WizardViewCreator<ActiveLearnJsLoopEndViewRepresentation, ActiveLearnJsLoopViewValue> getViewCreator() {
        return new JavaScriptViewCreator<>(getJavascriptObjectID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return false;
    }
}
