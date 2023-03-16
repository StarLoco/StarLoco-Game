local npc = Npc(94, 9030)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(291, {231})
    elseif answer == 231 then p:ask(292, {232, 233})
    end
end

RegisterNPCDef(npc)
