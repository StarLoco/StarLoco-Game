local npc = Npc(94, 9030)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(291, {231})
    elseif answer == 231 then p:ask(292, {232, 233})
    elseif answer == 232 then p:ask(293)
    elseif answer == 233 then p:ask(294)
    end
end

RegisterNPCDef(npc)
