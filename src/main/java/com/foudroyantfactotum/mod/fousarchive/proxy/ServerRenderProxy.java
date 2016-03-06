/*
 * Copyright (c) 2016 Foudroyant Factotum
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.foudroyantfactotum.mod.fousarchive.proxy;

import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

public class ServerRenderProxy implements IModRenderProxy
{
    @Override
    public InputStream getInputStream(ResourceLocation rl) throws IOException
    {
        return getClass().getClassLoader().getResourceAsStream(String.format("assets/%s/%s", rl.getResourceDomain(), rl.getResourcePath()));
    }
}
