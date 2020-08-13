/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.plugins.setIp

import com.intellij.debugger.impl.DebuggerManagerListener
import com.intellij.debugger.impl.DebuggerSession
import com.intellij.ide.plugins.PluginManagerConfigurable
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.xdebugger.impl.XDebugSessionImpl

class SetIPStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {

        Notification(
                "",
                "SetIP rename and release notification",
                "SetIP Plugin has been released with new name Jump to Line. Please <b>remove SetIP</b> plugin and install <b>JumpToLine plugin</b> using <a href=\"\">MarketPlace</a> to take further updates.",
                NotificationType.INFORMATION
        ).also { it.setListener { _, _  -> PluginManagerConfigurable.showPluginConfigurable(project) } }
                .notify(project)

        val debuggerListener = object : DebuggerManagerListener {
            override fun sessionAttached(session: DebuggerSession?) {
                 val xSession = session?.xDebugSession as? XDebugSessionImpl ?: return
                val typeResolver = CommonTypeResolver(session.project)
                val sessionHandler = SetIPSessionEvenHandler(session, xSession, typeResolver)
                xSession.addSessionListener(sessionHandler)
            }
        }

        project.messageBus.connect().subscribe(DebuggerManagerListener.TOPIC, debuggerListener)
    }
}