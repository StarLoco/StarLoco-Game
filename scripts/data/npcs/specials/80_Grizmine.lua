local npc = Npc(80, 9025)
--TODO: Faire les actions des dialogs des minis-jeux > aller sniffer offi
--TODO: on peut gagner que avec le buff clairvoyance
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(134, {121, 1003, 138, 116})
    elseif answer == 116 then p:ask(135, {118, 141})
    elseif answer == 118 then p:ask(136, {131})
    elseif answer == 131 then p:ask(164)
    elseif answer == 141 then p:ask(134, {121, 1003, 138, 116})
    elseif answer == 121 then p:ask(144, {122, 130, 141})
    elseif answer == 130 then p:ask(145, {124, 126, 127, 128})
    elseif answer == 128 then p:ask(164)
    elseif answer == 124 then p:ask(140)
    elseif answer == 126 then p:ask(164)
    elseif answer == 127 then p:ask(164)
    elseif answer == 122 then p:ask(138, {133, 134, 135, 136})
    elseif answer == 133 then p:ask(140)
    elseif answer == 134 then p:ask(164)
    elseif answer == 135 then p:ask(164)
    elseif answer == 136 then p:ask(164)
    elseif answer == 141 then p:ask(134, {121, 1003, 138, 116})
    elseif answer == 138 then p:ask(7918, {9636, 139})
    elseif answer == 9636 then p:ask(9649)
    elseif answer == 139 then p:ask(7919)
    elseif answer == 1003 then p:ask(1345, {1004, 100})
    elseif answer == 1004 then p:ask(1346, {1006, 1007, 100})
    elseif answer == 1006 then p:ask(1348, {1010, 101})
    elseif answer == 1010 then p:ask(1351, {1017, 1016, 101})
    elseif answer == 1016 then p:ask(1355)
    elseif answer == 1017 then p:ask(1356)
    elseif answer == 1007 then p:endDialog()
    end
end

RegisterNPCDef(npc)
