local npc = Npc(80, 9025)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(134, {121, 1003, 138, 116})
    elseif answer == 116 then p:ask(135, {118, 141})
    elseif answer == 121 then p:ask(144, {122, 130, 141})
    elseif answer == 138 then p:ask(7918, {9636, 139})
    elseif answer == 1003 then p:ask(1345, {1004, 100})
    end
end

RegisterNPCDef(npc)