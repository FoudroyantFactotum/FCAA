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
package mod.fou.fcaa.utility;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;

public class Clazz
{
    public static ImmutableSet<ClassPath.ClassInfo> getClassListFrom(Package p)
    {
        try
        {
            return ClassPath.from(ClassLoader.getSystemClassLoader())
                    .getTopLevelClassesRecursive(p.getName());
        } catch (IOException e)
        {
            return ImmutableSet.of();
        }
    }
}
