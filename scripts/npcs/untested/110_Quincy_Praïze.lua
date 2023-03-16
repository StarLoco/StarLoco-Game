local npc = Npc(110, 9043)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(367, {301, 300})
    elseif answer == 300 then p:ask(368)
    elseif answer == 301 then p:ask(369)
    end
end

RegisterNPCDef(npc)