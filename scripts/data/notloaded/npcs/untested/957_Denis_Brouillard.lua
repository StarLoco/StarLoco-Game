local npc = Npc(957, 9110)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4235, {371})
    elseif answer == 371 then p:ask(444)
    end
end

RegisterNPCDef(npc)
