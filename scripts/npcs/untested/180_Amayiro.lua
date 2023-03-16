local npc = Npc(180, 9038)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(711, {664})
    elseif answer == 664 then p:ask(920)
    end
end

RegisterNPCDef(npc)
