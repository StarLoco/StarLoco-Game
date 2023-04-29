local npc = Npc(90, 9023)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(278, {224})
    elseif answer == 224 then p:ask(281)
    end
end

RegisterNPCDef(npc)
