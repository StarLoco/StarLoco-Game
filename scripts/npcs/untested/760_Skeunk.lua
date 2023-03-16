local npc = Npc(760, 1450)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3143, {2766})
    elseif answer == 2766 then p:ask(3144, {6628, 2767})
    end
end

RegisterNPCDef(npc)
