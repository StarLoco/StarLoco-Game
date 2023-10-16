local npc = Npc(78, 1010)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(411, {1269, 333, 334})
    elseif answer == 1269 then p:ask(108, {7569, 7560, 7543})
    elseif answer == 7569 then p:ask(7576, {7570, 7571, 7572, 7573})
    elseif answer == 7570 then
        p:compassTo(951)
        p:endDialog()
    elseif answer == 7571 then
        p:compassTo(40)
        p:endDialog()
    elseif answer == 7572 then p:compassTo(33)
    elseif answer == 7573 then p:ask(7577, {7574, 7575, 7576, 7577})
    elseif answer == 7574 then
        p:compassTo(9459)
        p:endDialog()
    elseif answer == 7575 then p:compassTo(226)
    elseif answer == 7576 then p:compassTo(494)
    elseif answer == 7577 then p:ask(108, {7569, 7560, 7543})
    elseif answer == 7560 then p:ask(7574, {7561, 7562, 7563, 7564})
    elseif answer == 7561 then
        p:compassTo(7411)
        p:endDialog()
    elseif answer == 7562 then
        p:compassTo(4265)
        p:endDialog()
    elseif answer == 7563 then
        p:compassTo(4551)
        p:endDialog()
    elseif answer == 7564 then p:ask(7574, {7565, 7566, 7567, 7568})
    elseif answer == 7543 then p:ask(7570, {7544, 7545, 7546, 7547})
    elseif answer == 7544 then
        p:compassTo(540)
        p:endDialog()
    elseif answer == 7545 then
        p:compassTo(745)
        p:endDialog()
    elseif answer == 7546 then
        p:compassTo(487)
        p:endDialog()
    elseif answer == 7547 then p:ask(7571, {7548, 7549, 7550, 7551})
    elseif answer == 7548 then
        p:compassTo(686)
        p:endDialog()
    elseif answer == 7549 then
        p:compassTo(177)
        p:endDialog()
    elseif answer == 7550 then
        p:compassTo(518)
        p:endDialog()
    elseif answer == 7551 then p:ask(7572, {7552, 7553, 7554, 7555})
    elseif answer == 7552 then
        p:compassTo(490)
        p:endDialog()
    elseif answer == 7553 then
        p:compassTo(8207)
        p:endDialog()
    elseif answer == 7554 then
        p:compassTo(2061)
        p:endDialog()
    elseif answer == 7555 then p:ask(7573, {7556, 7557, 7558, 7559})
    elseif answer == 7556 then
        p:compassTo(684)
        p:endDialog()
    elseif answer == 7557 then
        p:compassTo(690)
        p:endDialog()
    elseif answer == 7558 then
        p:compassTo(50)
        p:endDialog()
    elseif answer == 7559 then p:ask(108, {7569, 7560, 7543})
    elseif answer == 7565 then
        p:compassTo(8037)
        p:endDialog()
    elseif answer == 7566 then
        p:compassTo(1158)
        p:endDialog()
    elseif answer == 7567 then
        p:compassTo(8785)
        p:endDialog()
    elseif answer == 7568 then p:ask(108, {7569, 7560, 7543})
    elseif answer == 333 then p:ask(414)
    elseif answer == 334 then p:ask(415, {337, 338, 340})
    elseif answer == 337 then p:ask(417)
    elseif answer == 338 then p:ask(418)
    elseif answer == 340 then p:ask(420)
    end
end

RegisterNPCDef(npc)
