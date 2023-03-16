local npc = Npc(118, 9037)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(384, {309})
    elseif answer == 309 then p:ask(385)
    end
end

RegisterNPCDef(npc)