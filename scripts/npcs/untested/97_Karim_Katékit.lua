local npc = Npc(97, 9046)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(300, {242, 237})
    elseif answer == 242 then p:ask(307)
    elseif answer == 237 then p:ask(302, {244, 238})
    end
end

RegisterNPCDef(npc)