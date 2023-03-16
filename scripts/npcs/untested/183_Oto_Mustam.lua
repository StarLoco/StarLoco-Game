local npc = Npc(183, 1206)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(811, {728})
    elseif answer == 728 then p:ask(1031, {740})
    end
end

RegisterNPCDef(npc)