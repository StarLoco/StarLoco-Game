local npc = Npc(840, 1241)

npc.gender = 1
npc.scaleX = 110
npc.scaleY = 110

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3518)
    end
end

RegisterNPCDef(npc)
