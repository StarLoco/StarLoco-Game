local npc = Npc(644, 1264)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2600, {2222, 2432})
    elseif answer == 2432 then p:ask(2815)
    elseif answer == 2222 then p:ask(2601)
    end
end

RegisterNPCDef(npc)
