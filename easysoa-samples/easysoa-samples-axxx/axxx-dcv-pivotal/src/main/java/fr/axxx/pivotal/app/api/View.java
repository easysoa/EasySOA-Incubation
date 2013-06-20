/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */

package fr.axxx.pivotal.app.api;

import fr.axxx.pivotal.app.model.User;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.osoa.sca.annotations.Service;

/**
 *
 * @author jguillemotte
 */
@Service
public interface View {

    /**
     * Render method to serve the page content. 
     * The 'render' macro is called in the correspondonding velocity template.
     * @param params A Map containing parameters for the page display
     * @param request Http request
     * @param user Connected user
     * @return The page content
     */
    public String render(Map<String, Object> params, HttpServletRequest request, User user);

}
