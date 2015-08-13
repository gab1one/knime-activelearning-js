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
 *   Aug 2, 2015 (gabriel): created
 */
package org.knime.al.js.util.server;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.imageio.ImageIO;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.renderer.DataValueRenderer;
import org.knime.core.data.renderer.DataValueRendererFactory;
import org.knime.core.node.NodeLogger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

/**
 *
 * @author gabriel
 */
public class ALFileServer extends NanoHTTPD {

    private final NodeLogger m_logger = NodeLogger
            .getLogger(ALFileServer.class);

    private static final String PNG = "image/png";
    private static final String TEXT = "text/plain";

    private static final Dimension dims = new Dimension(250, 250);

    private final Map<String, DataCell> m_cells;

    private DataColumnSpec spec;

    /**
     * @param port
     *            the port the server will run at
     * @param dataCells
     *            the map to the cells
     */
    public ALFileServer(final int port, final Map<String, DataCell> dataCells) {
        super(port);

        m_cells = dataCells;
        try {
            start();
        } catch (final IOException e) {
            m_logger.error("Can't start the webserver!");
        }
    }

    @Override
    public Response serve(final IHTTPSession session) {

        final Method method = session.getMethod();

        if (method != Method.GET) { // not a file request
            return getForbiddenResponse(method.toString());
        }

        // strip "/" from url
        final String rowID = session.getUri().replace("/", "");

        // abort early
        if (!m_cells.containsKey(rowID)) {
            return getNotFoundResponse();
        }

        final DataCell cell = m_cells.get(rowID);
        final ByteArrayInputStream bos;
        try {
            bos = createImage(cell);
        } catch (final IOException e) {
            return getInternalErrorResponse("Could not create output Image.");
        }
        return newChunkedResponse(Status.OK, PNG, bos);
    }

    private ByteArrayInputStream createImage(final DataCell cell)
            throws IOException {
        final Collection<DataValueRendererFactory> renderFactories = cell
                .getType().getRendererFactories();

        final DataValueRenderer renderer = renderFactories.iterator().next()
                .createRenderer(spec); // get the preferred renderer
        final Component comp = renderer.getRendererComponent(cell);

        comp.setSize(dims);

        final BufferedImage image = new BufferedImage(dims.width, dims.height,
                BufferedImage.TYPE_INT_ARGB);

        // create graphics object to paint in
        final Graphics2D graphics = image.createGraphics();
        comp.paint(graphics);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
        ImageIO.write(image, "png", bos);
        bos.close();

        return new ByteArrayInputStream(bos.toByteArray()); // FIXME find better
                                                            // method to do
                                                            // this.
    }

    /**
     * @param message
     *            an additional message
     * @return A response indicating that the requested action is forbidden.
     */
    protected Response getForbiddenResponse(final String message) {
        m_logger.warn("Blocked forbidden request: " + message);
        return newFixedLengthResponse(Response.Status.FORBIDDEN,
                NanoHTTPD.MIME_PLAINTEXT,
                "FORBIDDEN: " + message + "\n You may only send GET requests");
    }

    /**
     * @return a 404 Response
     */
    protected Response getNotFoundResponse() {
        m_logger.warn("");

        return newFixedLengthResponse(Response.Status.NOT_FOUND,
                NanoHTTPD.MIME_PLAINTEXT, "Error 404, row not found.");
    }

    /**
     * @param message
     *            the message
     * @return the Internal Error Response
     */
    protected Response getInternalErrorResponse(final String message) {
        m_logger.error(
                "An internal error occured in the Webserver: " + message);
        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR,
                NanoHTTPD.MIME_PLAINTEXT, "INTERNAL ERROR: " + message);
    }

}
