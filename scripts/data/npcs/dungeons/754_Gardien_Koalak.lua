local npc = Npc(754, 1363)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if p:mapID() == 8905 then
        local cell = p:cell()
        local orientation = p:orientation()
        if not (cell == 213 and orientation == 5) then
            p:ask(3161)
            return
        end
        p:ask(3119, {}, "[name]")
    end
end

RegisterNPCDef(npc)
