//  MIT License
//  
//  Copyright (c) 2019 fren_gor
//  
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//  
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//  
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.

package com.fren_gor.commandCraftCore;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.fren_gor.commandCraftCore.events.RegisterVariablesEvent;
import com.fren_gor.commandCraftCore.events.events.OnDisableEvent;
import com.fren_gor.commandCraftCore.events.events.OnEnableEvent;
import com.fren_gor.commandCraftCore.vars.BooleanVar;
import com.fren_gor.commandCraftCore.vars.PlayerVar;
import com.fren_gor.commandCraftCore.vars.StringVar;
import com.fren_gor.commandCraftCore.vars.Type;

class EventVarsRegisterListner implements Listener {

	public EventVarsRegisterListner() {
		Bukkit.getPluginManager().registerEvents(this, CommandCraftCore.getInstance());
	}

	@EventHandler
	public void onReg(RegisterVariablesEvent e) {

		// PlayerJoinEvent
		e.registerVariable(PlayerJoinEvent.class, "playerName", Type.STRING,
				(ev, v) -> new StringVar(v, "playerName", ((PlayerJoinEvent) ev).getPlayer().getName()).setFinal(),
				true);

		e.registerVariable(PlayerJoinEvent.class, "player", Type.PLAYER,
				(ev, v) -> new PlayerVar(v, "player", ((PlayerJoinEvent) ev).getPlayer()).setFinal(), true);

		e.registerVariable(PlayerJoinEvent.class, "joinMessage", Type.STRING,
				(ev, v) -> new StringVar(v, "joinMessage", ((PlayerJoinEvent) ev).getJoinMessage()).setConst());

		e.registerEventTask(PlayerJoinEvent.class, (ev, v, b) -> {
			if (b || ((String) v.getVar("joinMessage").get()).isEmpty()) {
				((PlayerJoinEvent) ev).setJoinMessage(null);
			} else
				((PlayerJoinEvent) ev).setJoinMessage((String) v.getVar("joinMessage").get());
		}, "Reassign $joinMessage to change the displayed join message",
				"Cancel the event to do not display any join message");
		// End PlayerJoinEvent

		// OnDisableEvent
		e.registerVariable(OnDisableEvent.class, "reloading", Type.BOOLEAN,
				"true if server is reloading, false if the server is shutting down",
				(ev, v) -> new BooleanVar(v, "reloading", ((OnDisableEvent) ev).isReloading()).setFinal(), true);
		// End OnDisableEvent

		// OnEnableEvent
		e.registerEvent(OnEnableEvent.class);
		// End OnEnableEvent

		// PlayerQuitEvent
		e.registerVariable(PlayerQuitEvent.class, "playerName", Type.STRING,
				(ev, v) -> new StringVar(v, "playerName", ((PlayerQuitEvent) ev).getPlayer().getName()).setFinal(),
				true);

		e.registerVariable(PlayerQuitEvent.class, "player", Type.PLAYER,
				(ev, v) -> new PlayerVar(v, "player", ((PlayerQuitEvent) ev).getPlayer()).setFinal(), true);

		e.registerVariable(PlayerQuitEvent.class, "quitMessage", Type.STRING,
				(ev, v) -> new StringVar(v, "quitMessage", ((PlayerQuitEvent) ev).getQuitMessage()).setConst());

		e.registerEventTask(PlayerQuitEvent.class, (ev, v, b) -> {
			if (b || ((String) v.getVar("quitMessage").get()).isEmpty()) {
				((PlayerQuitEvent) ev).setQuitMessage(null);
			} else
				((PlayerQuitEvent) ev).setQuitMessage((String) v.getVar("quitMessage").get());
		}, "Reassign $quitMessage to change the displayed quit message",
				"Cancel the event to do not display any quit message");
		// End PlayerQuitEvent

		// PlayerKickEvent
		e.registerVariable(PlayerKickEvent.class, "playerName", Type.STRING,
				(ev, v) -> new StringVar(v, "playerName", ((PlayerKickEvent) ev).getPlayer().getName()).setFinal(),
				true);

		e.registerVariable(PlayerKickEvent.class, "player", Type.PLAYER,
				(ev, v) -> new PlayerVar(v, "player", ((PlayerKickEvent) ev).getPlayer()).setFinal(), true);

		e.registerVariable(PlayerKickEvent.class, "leaveMessage", Type.STRING,
				(ev, v) -> new StringVar(v, "leaveMessage", ((PlayerKickEvent) ev).getLeaveMessage()).setConst());

		e.registerVariable(PlayerKickEvent.class, "kickReason", Type.STRING,
				(ev, v) -> new StringVar(v, "kickReason", ((PlayerKickEvent) ev).getReason()).setConst());

		e.registerEventTask(PlayerKickEvent.class, (ev, v, b) -> {
			if (!((String) v.getVar("leaveMessage").get()).isEmpty()) {
				((PlayerKickEvent) ev).setLeaveMessage((String) v.getVar("leaveMessage").get());
			}
			if (!((String) v.getVar("kickReason").get()).isEmpty()) {
				((PlayerKickEvent) ev).setReason((String) v.getVar("kickReason").get());
			}
		}, "Reassign $leaveMessage to change the displayed leave message",
				"Reassign $kickReason to change the displayed kick reason");

		e.registerEventDescription(PlayerKickEvent.class, "Cancel the event to cancel the kick");
		// End PlayerKickEvent

	}

}
